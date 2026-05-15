import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../../services/user.service';
import { User } from '../../models/user.model'; // Import from central model

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})
export class UserListComponent implements OnInit {
  users: User[] = [];
  error: string | null = null;

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.error = null;
    this.userService.getUsers().subscribe({
      next: (data) => {
        this.users = data; // No need to cast anymore
      },
      error: (err) => {
        console.error('Failed to load users', err);
        // Error handling is now also done by interceptor, but local error state is fine for UI
        this.error = 'Could not load user data.';
      }
    });
  }

  // Updated to use the new unified service method
  suspendUser(userId: number): void {
    this.userService.updateUserStatus(userId, 'suspended').subscribe({
      next: () => {
        console.log(`User ${userId} suspended successfully`);
        this.loadUsers(); // Refresh the list
      },
      error: (err) => console.error(`Failed to suspend user ${userId}`, err)
    });
  }

  // Updated to use the new unified service method
  activateUser(userId: number): void {
    this.userService.updateUserStatus(userId, 'active').subscribe({
      next: () => {
        console.log(`User ${userId} activated successfully`);
        this.loadUsers(); // Refresh the list
      },
      error: (err) => console.error(`Failed to activate user ${userId}`, err)
    });
  }
}
