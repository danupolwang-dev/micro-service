import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router'; // Import RouterLink
import { UserService } from '../../services/user.service';
import { CustomerService } from '../../services/customer.service';
import { ProductService, Product } from '../../services/product.service';
import { AuthService } from '../../services/auth.service';
import { DashboardStats } from '../../models/dashboard.model';
import { User } from '../../models/user.model';
import { Customer } from '../../models/customer.model';
import { forkJoin, of } from 'rxjs';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink], // Add RouterLink here
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  stats: DashboardStats = {
    totalUsers: 0,
    totalCustomers: 0,
    totalProducts: 0
  };

  userCount: number = 0;
  recentUsers: User[] = [];
  recentCustomers: Customer[] = [];
  recentProducts: Product[] = [];
  timeRange: string = 'today';

  isLoading = true;
  error: string | null = null;
  isAdmin = false;

  constructor(
    private userService: UserService,
    private customerService: CustomerService,
    private productService: ProductService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.isAdmin = this.authService.isAdmin();
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.isLoading = true;
    this.error = null;

    const users$ = this.isAdmin ? this.userService.getUsers() : of([]);
    const userCount$ = this.isAdmin ? this.userService.getUserCount(this.timeRange) : of(0);
    const recentUsers$ = this.isAdmin ? this.userService.getRecentUsers() : of([]);

    const customers$ = this.customerService.getAllCustomers();
    const recentCustomers$ = this.customerService.getRecentCustomers();
    const products$ = this.productService.getAllProducts();
    const recentProducts$ = this.productService.getRecentProducts();

    forkJoin({
      users: users$,
      userCount: userCount$,
      recentUsers: recentUsers$,
      customers: customers$,
      recentCustomers: recentCustomers$,
      products: products$,
      recentProducts: recentProducts$
    }).subscribe({
      next: (data) => {
        const productCount = (data.products as any).totalElements !== undefined
                             ? (data.products as any).totalElements
                             : (Array.isArray(data.products) ? data.products.length : 0);

        this.stats = {
          totalUsers: Array.isArray(data.users) ? data.users.length : 0,
          totalCustomers: Array.isArray(data.customers) ? data.customers.length : 0,
          totalProducts: productCount
        };

        this.userCount = data.userCount;
        this.recentUsers = Array.isArray(data.recentUsers) ? data.recentUsers : [];
        this.recentCustomers = Array.isArray(data.recentCustomers) ? data.recentCustomers : [];
        this.recentProducts = Array.isArray(data.recentProducts) ? data.recentProducts : [];

        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        console.error('Error loading dashboard data', err);
        this.error = 'Failed to load dashboard data. Please try again later.';
      }
    });
  }

  onRangeChange(range: string): void {
    if (!this.isAdmin) return;

    this.timeRange = range;
    this.userService.getUserCount(range).subscribe({
      next: (count) => {
        this.userCount = count;
      },
      error: (err) => {
        console.error('Failed to update user count', err);
      }
    });
  }
}
