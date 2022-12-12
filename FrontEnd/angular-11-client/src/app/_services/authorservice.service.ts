import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TokenStorageService } from './token-storage.service';


const API_URL = 'http://localhost:8091/api/v1/digitalbooks/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
@Injectable({
  providedIn: 'root'
})
export class AuthorserviceService {

  constructor(private http: HttpClient, private tokenService: TokenStorageService) { }

  user = this.tokenService.getUser();

  createBook(book: any) : Observable<any> {
    return this.http.post(API_URL +"author/"+this.user.id+"/books",book,httpOptions);
  }
  updateBook(book: any) : Observable<any> {
    return this.http.put(API_URL +"author/"+this.user.id+"/updateBook/"+book.id,book,httpOptions);
  }

}
