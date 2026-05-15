import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup;
  loginError: string | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      // The form control name 'username' matches what the backend expects.
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  onSubmit() {
    this.loginError = null;
    if (this.loginForm.invalid) {
      return;
    }

    // ** THE FIX IS HERE **
    // The security-service expects a payload with a 'username' field (all lowercase).
    // this.loginForm.value already produces the correct payload: { username: '...', password: '...' }
    // We send the form value directly.
    this.authService.login(this.loginForm.value).subscribe({
      next: (response) => {
        console.log('Login successful', response);

        // The authService's tap operator already handles storing the token.
        // We just need to navigate.
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        console.error('Login failed', err);
        // Provide a more specific error message if the backend sends one
        this.loginError = err.error?.message || 'Invalid username or password.';
      }
    });
  }
}
