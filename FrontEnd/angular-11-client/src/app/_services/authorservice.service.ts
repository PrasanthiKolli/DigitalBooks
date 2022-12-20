import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TokenStorageService } from './token-storage.service';


const API_URL = 'http://localhost:8091/api/v1/digitalbooks/';
const AWS_URL="https://if64fdja59.execute-api.ap-northeast-1.amazonaws.com/UAT/";
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json','Access-Control-Allow-Methods': 'POST, PUT, GET, OPTIONS'})
};
const headers= new HttpHeaders({ 'Content-Type': 'application/json',
'Access-Control-Allow-Origin':"*" ,'Access-Control-Allow-Methods': 'POST, PUT, GET, OPTIONS'});
@Injectable({
  providedIn: 'root'
})
export class AuthorserviceService {

  constructor(private http: HttpClient, private tokenService: TokenStorageService) { }

  user = this.tokenService.getUser();

  createBook(book: any) : Observable<any> {
    return this.http.post(AWS_URL +"login/"+this.user.id,book,httpOptions);
  }
  updateBook(book: any) : Observable<any> {
    return this.http.put(AWS_URL +"login/"+this.user.id+book.id,book,httpOptions);
  }
  getBooksCreatedByAuthor(id: any) : Observable<any> {
    return this.http.get(AWS_URL + 'login/'+id);
  }

  blockBook(bookId: any, block: any,book:any) {
    let queryParams = new HttpParams();
    queryParams = queryParams.append("block",block)
    return this.http.post(AWS_URL +"login/"+this.user.id+bookId,book,{params:queryParams});
  }


}
