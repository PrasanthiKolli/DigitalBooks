import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { TokenStorageService } from '../_services/token-storage.service';
import { UserService } from '../_services/user.service';


@Component({
  selector: 'app-searchform',
  templateUrl: './searchform.component.html',
  styleUrls: ['./searchform.component.css']
})
export class SearchformComponent  {

  isSearchSuccess = false;
  errorMessage = "";
  isUserLoggedIn = this.tokenService.getUser() !== null;

  searchForm : any = {
    category:null,
    title:null,
    author:null,
    price:null,
    publisher:null
  };
  
  books : any[] = []
  
  book : any = {
    logo: null,
    title: null,
    publisher: null,
    category: null,
    content: null,
    price: null
  }

  constructor(private userService: UserService, private tokenService: TokenStorageService) { }

  onSearch(){
    const {category, title, author,price,publisher} = this.searchForm;
    this.userService.search(category, title, author,price,publisher).subscribe(
      data => {
        console.log(data);
        this.isSearchSuccess = true;
        this.books.push(data);
      },
      error => {
        console.error(error);
        this.isSearchSuccess = false;
        if(error instanceof HttpErrorResponse){
          console.error(error.error.message);
          this.errorMessage = error.error.message
        }
        
      }
    );

  }

  onClick(book:any){
    if(!this.isUserLoggedIn){
      console.log("please login/signup");
    }
  }

  oncancelSearch(){
    this.isSearchSuccess = false;
  }


}
