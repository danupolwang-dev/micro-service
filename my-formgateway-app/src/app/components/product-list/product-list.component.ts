import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Product, ProductService } from '../../services/product.service';
import { ToastService } from '../../services/toast.service';
import { ConfirmService } from '../../services/confirm.service';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Component({
    selector: 'app-product-list',
    standalone: true,
    imports: [CommonModule, RouterModule, FormsModule],
    templateUrl: './product-list.component.html',
    styleUrls: ['./product-list.component.css']
})
export class ProductListComponent implements OnInit {
    products: Product[] = [];
    loading = true;
    error: string | null = null;

    // Search & Pagination State
    searchTerm: string = '';
    currentPage: number = 0;
    pageSize: number = 10;
    totalPages: number = 0;
    totalElements: number = 0;

    private searchSubject = new Subject<string>();

    constructor(
        private productService: ProductService,
        private toastService: ToastService,
        private confirmService: ConfirmService
    ) {
        // Set up search debouncing
        this.searchSubject.pipe(
            debounceTime(300),
            distinctUntilChanged()
        ).subscribe(term => {
            this.searchTerm = term;
            this.currentPage = 0;
            this.loadProducts();
        });
    }

    ngOnInit(): void {
        this.loadProducts();
    }

    loadProducts(): void {
        this.loading = true;
        this.productService.getAllProducts(this.currentPage, this.pageSize, this.searchTerm).subscribe({
            next: (data) => {
                this.products = data.content;
                this.totalPages = data.totalPages;
                this.totalElements = data.totalElements;
                this.loading = false;
            },
            error: (err) => {
                console.error('Error loading products', err);
                this.error = 'Failed to load products.';
                this.loading = false;
            }
        });
    }

    onSearchChange(term: string): void {
        this.searchSubject.next(term);
    }

    onPageChange(page: number): void {
        this.currentPage = page;
        this.loadProducts();
    }

    // Helper for pagination numbers
    getPages(): number[] {
        const pages = [];
        for (let i = 0; i < this.totalPages; i++) {
            pages.push(i);
        }
        return pages;
    }

    toggleStatus(product: Product): void {
        if (!product.id) return;

        const action = product.status === 'ACTIVE' ? 'suspend' : 'activate';
        const observable = product.status === 'ACTIVE'
            ? this.productService.suspendProduct(product.id)
            : this.productService.activateProduct(product.id);

        observable.subscribe({
            next: (updatedProduct) => {
                // Update local state
                product.status = updatedProduct.status;
            },
            error: (err) => {
                console.error(`Failed to ${action} product`, err);
                this.toastService.showError(`Failed to ${action} product.`);
            }
        });
    }

    async deleteProduct(id: number): Promise<void> {
        const confirmed = await this.confirmService.confirm(
            'Are you sure you want to delete this product? This action cannot be undone.',
            'Delete Product',
            'Delete'
        );

        if (confirmed) {
            this.productService.deleteProduct(id).subscribe({
                next: () => {
                    this.products = this.products.filter(p => p.id !== id);
                    this.toastService.showSuccess('Product deleted successfully.');
                },
                error: (err) => {
                    console.error('Failed to delete product', err);
                    this.toastService.showError('Failed to delete product.');
                }
            });
        }
    }
}
