import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Product, ProductService } from '../../services/product.service';
import { ToastService } from '../../services/toast.service';
import { MasterDataService, MasterComponent } from '../../services/master-data.service';

@Component({
    selector: 'app-product-form',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterModule],
    templateUrl: './product-form.component.html',
    styleUrls: ['./product-form.component.css']
})
export class ProductFormComponent implements OnInit {
    product: Product = {
        code: '',
        name: '',
        price: 0,
        categoryCode: '',
        status: 'ACTIVE'
    };
    categories: MasterComponent[] = [];
    isEditMode = false;
    isLoading = false;
    error: string | null = null;
    imagePreview: string | null = null;
    selectedFile: File | null = null;

    constructor(
        private productService: ProductService,
        private masterDataService: MasterDataService,
        private toastService: ToastService,
        private route: ActivatedRoute,
        private router: Router
    ) { }

    ngOnInit(): void {
        this.loadCategories();
        const id = this.route.snapshot.paramMap.get('id');
        if (id) {
            this.isEditMode = true;
            this.loadProduct(Number(id));
        }
    }

    loadCategories(): void {
        this.masterDataService.getDropdownItems('PRODUCT_CATEGORY').subscribe({
            next: (data) => this.categories = data,
            error: (err) => console.error('Failed to load categories', err)
        });
    }

    loadProduct(id: number): void {
        this.isLoading = true;
        this.productService.getProductById(id).subscribe({
            next: (data) => {
                this.product = data;
                if (data.imageUrl) {
                    this.imagePreview = data.imageUrl;
                }
                this.isLoading = false;
            },
            error: (err) => {
                console.error('Failed to load product', err);
                this.error = 'Failed to load product details.';
                this.isLoading = false;
            }
        });
    }

    onFileSelected(event: any): void {
        const file: File = event.target.files[0];
        if (file) {
            this.selectedFile = file;

            // Show preview
            const reader = new FileReader();
            reader.onload = () => {
                this.imagePreview = reader.result as string;
            };
            reader.readAsDataURL(file);
        }
    }

    removeImage(): void {
        this.selectedFile = null;
        this.imagePreview = null;
        this.product.imageId = undefined;
        this.product.imageUrl = undefined;
    }

    onSubmit(): void {
        this.isLoading = true;
        this.error = null;

        if (this.selectedFile) {
            // Upload image first
            this.productService.uploadImage(this.selectedFile).subscribe({
                next: (res) => {
                    this.product.imageId = res.id;
                    this.saveProduct();
                },
                error: (err) => {
                    console.error('Failed to upload image', err);
                    this.toastService.showError('Failed to upload image.');
                    this.isLoading = false;
                }
            });
        } else {
            this.saveProduct();
        }
    }

    private saveProduct(): void {
        const observable = this.isEditMode && this.product.id
            ? this.productService.updateProduct(this.product.id, this.product)
            : this.productService.createProduct(this.product);

        observable.subscribe({
            next: () => {
                this.isLoading = false;
                this.toastService.showSuccess(this.isEditMode ? 'Product updated successfully!' : 'Product created successfully!');
                this.router.navigate(['/products']);
            },
            error: (err) => {
                console.error('Failed to save product', err);
                this.error = 'Failed to save product. Please try again.';
                this.toastService.showError('Error saving product.');
                this.isLoading = false;
            }
        });
    }
}
