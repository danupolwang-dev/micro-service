import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface MasterComponent {
  id?: number;
  name: string;
  code: string;
  parent?: number;
  status: string;
}

@Injectable({
  providedIn: 'root'
})
export class MasterDataService {
  private apiUrl = `${environment.apiUrl}/api/products/components`;

  constructor(private http: HttpClient) { }

  getAllComponents(): Observable<MasterComponent[]> {
    return this.http.get<MasterComponent[]>(this.apiUrl);
  }

  getComponentsByParent(parentId: number): Observable<MasterComponent[]> {
    return this.http.get<MasterComponent[]>(`${this.apiUrl}/parent/${parentId}`);
  }

  getComponentsByCode(code: string): Observable<MasterComponent[]> {
    return this.http.get<MasterComponent[]>(`${this.apiUrl}/code/${code}`);
  }

  getDropdownItems(parentCode: string): Observable<MasterComponent[]> {
    return this.http.get<MasterComponent[]>(`${this.apiUrl}/list/${parentCode}`);
  }
}
