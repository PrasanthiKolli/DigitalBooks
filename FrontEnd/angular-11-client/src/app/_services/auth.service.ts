import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

const USER_API = 'http://localhost:8091/api/v1/digitalbooks/';
const AWS_API="https://if64fdja59.execute-api.ap-northeast-1.amazonaws.com/UAT/"
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(private http: HttpClient) { }

  // login(userName: string, password: string): Observable<any> {
  //   return this.http.post(USER_API + 'sign-in', {
  //     userName,
  //     password
  //   }, httpOptions);
  // }
  login(userName: string, password: string): Observable<any> {
    return this.http.post(AWS_API+"/login", {
      userName,
      password
    }, httpOptions);
  }

  // register(userName: string, emailId: string, password: string,phoneNumber:string,role:any): Observable<any> {
  //   return this.http.post(USER_API + 'sign-up', {
  //     userName,
  //     emailId,
  //     password,
  //     phoneNumber,
  //     role
  //   }, httpOptions);
  // }
  register(userName: string, emailId: string, password: string,phoneNumber:string,role:any): Observable<any> {
      return this.http.post(AWS_API , {
        userName,
        emailId,
        password,
        phoneNumber,
        role
      }, httpOptions);
    }
    
}
