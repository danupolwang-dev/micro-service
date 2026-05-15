import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router'; // Import RouterLink
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink // Add RouterLink here
  ],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  registerForm: FormGroup;
  passwordErrors: string[] = [];
  registrationError: string | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      username: ['', Validators.required],
      mobileNo: ['', Validators.required],
      password: ['', Validators.required],
      confirmPassword: ['', Validators.required]
    }, { validator: this.passwordMatchValidator });
  }

  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password');
    const confirmPassword = form.get('confirmPassword');
    return password && confirmPassword && password.value === confirmPassword.value ? null : { mismatch: true };
  }

  validatePassword(password: string): { isValid: boolean; errors: string[] } {
    const errors: string[] = [];
    if (password.length < 8) {
      errors.push('รหัสผ่านต้องมีความยาวอย่างน้อย 8 ตัวอักษร');
    }
    if (!/[A-Z]/.test(password)) {
      errors.push('รหัสผ่านต้องมีตัวพิมพ์ใหญ่อย่างน้อยหนึ่งตัว');
    }
    if (!/[a-z]/.test(password)) {
      errors.push('รหัสผ่านต้องมีตัวพิมพ์เล็กอย่างน้อยหนึ่งตัว');
    }
    if (!/[0-9]/.test(password)) {
      errors.push('รหัสผ่านต้องมีตัวเลขอย่างน้อยหนึ่งตัว');
    }
    if (!/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)) {
      errors.push('รหัสผ่านต้องมีอักขระพิเศษอย่างน้อยหนึ่งตัว');
    }
    return {
      isValid: errors.length === 0,
      errors: errors,
    };
  }

  onSubmit() {
    this.passwordErrors = [];
    this.registrationError = null;

    if (this.registerForm.invalid) {
      return;
    }

    const passwordValidation = this.validatePassword(this.registerForm.value.password);
    if (!passwordValidation.isValid) {
      this.passwordErrors = passwordValidation.errors;
      return;
    }

    // Exclude confirmPassword from the data sent to the backend
    const { confirmPassword, ...registrationData } = this.registerForm.value;

    this.authService.register(registrationData).subscribe({
      next: (response) => {
        console.log('Registration successful', response);
        // Redirect to login page after successful registration
        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.error('Registration failed', err);
        this.registrationError = err.error?.message || 'An unknown error occurred.';
      }
    });
  }
}
