import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Product {
  id?: number;
  code: string;
  name: string;
  categoryCode?: string;
  price: number;
  status?: string;
  imageId?: number; // Added to link with uploaded image
  imageUrl?: string; // For displaying
}

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiUrl = `${environment.apiUrl}/api/products`;
  private uploadUrl = `${environment.apiUrl}/api/products/images/upload`;

  constructor(private http: HttpClient) { }

  getAllProducts(page: number = 0, size: number = 10, query: string = ''): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (query) {
      params = params.set('query', query);
    }

    return this.http.get<any>(this.apiUrl, { params });
  }

  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/${id}`);
  }

  createProduct(product: Product): Observable<Product> {
    return this.http.post<Product>(this.apiUrl, product);
  }

  updateProduct(id: number, product: Product): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/${id}`, product);
  }

  suspendProduct(id: number): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/${id}/suspend`, {});
  }

  activateProduct(id: number): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/${id}/activate`, {});
  }

  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  uploadImage(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<any>(this.uploadUrl, formData);
  }

  getProductCount(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/stats/count`);
  }

  getRecentProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/recent`);
  }
}
