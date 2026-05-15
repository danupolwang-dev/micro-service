import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CustomerService } from '../../services/customer.service';
import { Customer } from '../../models/customer.model';

@Component({
    selector: 'app-customer-form',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterModule],
    templateUrl: './customer-form.component.html',
    styleUrls: ['./customer-form.component.css']
})
export class CustomerFormComponent implements OnInit {
    customer: Customer = {
        id: 0,
        name: '',
        email: '',
        mobileNo: '',
        status: 'ACTIVE'
    };

    isEditMode: boolean = false;
    isLoading: boolean = false;
    customerId: number | null = null;

    constructor(
        private customerService: CustomerService,
        private route: ActivatedRoute,
        private router: Router
    ) { }

    ngOnInit(): void {
        this.route.params.subscribe(params => {
            if (params['id']) {
                this.isEditMode = true;
                this.customerId = +params['id'];
                this.loadCustomer(this.customerId);
            }
        });
    }

    loadCustomer(id: number): void {
        this.isLoading = true;
        this.customerService.getCustomerById(id).subscribe({
            next: (data) => {
                this.customer = data;
                this.isLoading = false;
            },
            error: (err) => {
                console.error('Failed to load customer', err);
                this.isLoading = false;
            }
        });
    }

    onSubmit(): void {
        this.isLoading = true;

        if (this.isEditMode && this.customerId) {
            this.customerService.updateCustomer(this.customerId, this.customer).subscribe({
                next: () => {
                    console.log('Customer updated successfully');
                    this.router.navigate(['/customers']);
                },
                error: (err) => {
                    console.error('Failed to update customer', err);
                    this.isLoading = false;
                }
            });
        } else {
            this.customerService.createCustomer(this.customer).subscribe({
                next: () => {
                    console.log('Customer created successfully');
                    this.router.navigate(['/customers']);
                },
                error: (err) => {
                    console.error('Failed to create customer', err);
                    this.isLoading = false;
                }
            });
        }
    }
}
