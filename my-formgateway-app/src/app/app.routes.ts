import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { UserListComponent } from './components/user-list/user-list.component';
import { ProductListComponent } from './components/product-list/product-list.component';
import { ProductFormComponent } from './components/product-form/product-form.component';
import { CustomerListComponent } from './components/customer-list/customer-list.component';
import { CustomerFormComponent } from './components/customer-form/customer-form.component';
import { roleGuard } from './guards/role.guard';

export const routes: Routes = [
    { path: '', redirectTo: '/login', pathMatch: 'full' },
    { path: 'login', component: LoginComponent },
    { path: 'register', component: RegisterComponent },
    {
        path: 'dashboard',
        component: DashboardComponent,
        canActivate: [roleGuard],
        data: { roles: [] }
    },
    {
        path: 'users',
        component: UserListComponent,
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] } // Assuming User Management is Admin only
    },
    {
        path: 'products',
        component: ProductListComponent,
        canActivate: [roleGuard],
        data: { roles: [] }
    },
    {
        path: 'products/new',
        component: ProductFormComponent,
        canActivate: [roleGuard],
        data: { roles: [] }
    },
    {
        path: 'products/:id/edit',
        component: ProductFormComponent,
        canActivate: [roleGuard],
        data: { roles: [] }
    },
    {
        path: 'customers',
        component: CustomerListComponent,
        canActivate: [roleGuard],
        data: { roles: [] }
    },
    {
        path: 'customers/new',
        component: CustomerFormComponent,
        canActivate: [roleGuard],
        data: { roles: [] }
    },
    {
        path: 'customers/edit/:id',
        component: CustomerFormComponent,
        canActivate: [roleGuard],
        data: { roles: [] }
    }
];
