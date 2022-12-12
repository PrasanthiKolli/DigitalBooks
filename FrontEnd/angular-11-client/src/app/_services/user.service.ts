import { Injectable } from '@angular/core';
import { HttpClient ,HttpParams} from '@angular/common/http';
import { Observable } from 'rxjs';

const API_URL = 'http://localhost:8080/api/test/';
const url="http://localhost:8091/api/v1/digitalbooks/";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  constructor(private http: HttpClient) { }


  search(category: any, title: any, author: any, price: any, publisher: any) {
    console.log("title: "+title);
    let queryParams =new HttpParams();
    queryParams= queryParams.append("category",category)
                              .append("title",title)
                              .append("author",author)
                              .append("price",price)
                              .append("publisher",publisher);

    return this.http.get(url+"search",{params:queryParams});
  }

  getPublicContent(): Observable<any> {
    return this.http.get(API_URL + 'all', { responseType: 'text' });
  }

  getUserBoard(): Observable<any> {
    return this.http.get(API_URL + 'user', { responseType: 'text' });
  }

  getModeratorBoard(): Observable<any> {
    return this.http.get(API_URL + 'mod', { responseType: 'text' });
  }

  getAdminBoard(): Observable<any> {
    return this.http.get(API_URL + 'admin', { responseType: 'text' });
  }
}
