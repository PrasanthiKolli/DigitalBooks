import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TokenStorageService } from './token-storage.service';

const API_URL = 'http://localhost:8091/api/v1/digitalbooks/';
const AWS_URL="https://if64fdja59.execute-api.ap-northeast-1.amazonaws.com/UAT/";
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
@Injectable({
  providedIn: 'root'
})
export class ReaderService {

  constructor(private http: HttpClient, private tokenService: TokenStorageService) { }

  user = this.tokenService.getUser();

  getAllSubscribedBooks():Observable<any> {
    return this.http.get( API_URL+"reader/"+this.user.id);
  }
}
