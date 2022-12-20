import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TokenStorageService } from './token-storage.service';

const API_URL = 'http://localhost:8080/api/test/';
const url = "http://localhost:8091/api/v1/digitalbooks/";
const AWS_URL="https://if64fdja59.execute-api.ap-northeast-1.amazonaws.com/UAT/";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  

  constructor(private http: HttpClient,private tokenservice:TokenStorageService) { }


  search(category: any, title: any, author: any, price: any, publisher: any): Observable<any> {
    console.log("title: " + title);
    let reqParams = new HttpParams();
    reqParams = reqParams.append("category", category)
      .append("title", title)
      .append("author", author)
      .append("price", price)
      .append("publisher", publisher);

    //return this.http.get(url + "search", { params: reqParams });
    return this.http.get(AWS_URL, { params: reqParams });
  }
  subscribeABook(bookId: any, userId: any): Observable<any> {
    // return this.http.post(url + bookId + "/subscribe", {
    //   bookId: bookId, userId: userId
    // });
    return this.http.post(AWS_URL + bookId + "/subscribe", {
        bookId: bookId, userId: userId
      });
    
  }

  cancelSubscription(subId: number, userId: any) : Observable<any>{
    return this.http.post(AWS_URL +"reader/"+userId+subId,null);
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
  verifyIfLessThan24Hrs(bookId: any) : boolean{
    var currentTimestamp = Date.now();
    var twentyFourHours = 24 * 60 * 60 * 1000;
   
    let user = this.tokenservice.getUser();
    let subs =  user.subscriptions;
    for(let sub of subs){
      let subscriptionTimeStamp = Date.parse(sub.subscriptionTime);
      
      if(bookId === sub.bookId){
        if((currentTimestamp - subscriptionTimeStamp) > twentyFourHours){
          return false;
        }

      }
    }
    return true;
  }
}
