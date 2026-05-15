import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CustomerService } from '../../services/customer.service';
import { Customer } from '../../models/customer.model';

@Component({
    selector: 'app-customer-list',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './customer-list.component.html',
    styleUrls: ['./customer-list.component.css']
})
export class CustomerListComponent implements OnInit {
    customers: Customer[] = [];
    filteredCustomers: Customer[] = [];
    searchTerm: string = '';
    error: string | null = null;

    constructor(private customerService: CustomerService) { }

    ngOnInit(): void {
        this.loadCustomers();
    }

    loadCustomers(): void {
        this.error = null;
        this.customerService.getAllCustomers().subscribe({
            next: (data) => {
                this.customers = data;
                this.filteredCustomers = data;
            },
            error: (err) => {
                console.error('Failed to load customers', err);
                this.error = 'Could not load customer data.';
            }
        });
    }

    searchCustomers(event: Event): void {
        const target = event.target as HTMLInputElement;
        this.searchTerm = target.value.toLowerCase();

        this.filteredCustomers = this.customers.filter(customer =>
            customer.name.toLowerCase().includes(this.searchTerm) ||
            customer.email.toLowerCase().includes(this.searchTerm) ||
            customer.mobileNo.includes(this.searchTerm)
        );
    }

    suspendCustomer(id: number): void {
        this.customerService.suspendCustomer(id).subscribe({
            next: () => {
                console.log(`Customer ${id} suspended successfully`);
                this.loadCustomers();
            },
            error: (err) => console.error(`Failed to suspend customer ${id}`, err)
        });
    }

    activateCustomer(id: number): void {
        this.customerService.activateCustomer(id).subscribe({
            next: () => {
                console.log(`Customer ${id} activated successfully`);
                this.loadCustomers();
            },
            error: (err) => console.error(`Failed to activate customer ${id}`, err)
        });
    }
}
