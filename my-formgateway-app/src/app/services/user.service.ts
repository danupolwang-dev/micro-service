import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private usersApiUrl = `${environment.apiUrl}/api/users`;

  constructor(private http: HttpClient) { }

  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.usersApiUrl}/all`);
  }

  updateUserStatus(userId: number, status: 'suspended' | 'active'): Observable<User> {
    const payload = { status };
    return this.http.put<User>(`${this.usersApiUrl}/${userId}/status`, payload);
  }

  getUserCount(range: string): Observable<number> {
    return this.http.get<number>(`${this.usersApiUrl}/stats/count?range=${range}`);
  }

  getRecentUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.usersApiUrl}/recent`);
  }
}
