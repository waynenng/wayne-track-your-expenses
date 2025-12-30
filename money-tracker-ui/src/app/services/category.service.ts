import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Category {
  id: string;
  name: string;
  userId: string;
}

@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  private API_URL = 'http://localhost:8080/api/categories';

  constructor(private http: HttpClient) {}

  createCategory(category: Partial<Category>) {
    return this.http.post<Category>(
      this.API_URL,
      category
    );
  }

  getCategories(userId: string) {
    return this.http.get<Category[]>(
      `${this.API_URL}?userId=${userId}`
    );
  }

  addCategory(category: Omit<Category, 'id'>): Observable<Category> {
    return this.http.post<Category>(this.API_URL, category);
  }
}
