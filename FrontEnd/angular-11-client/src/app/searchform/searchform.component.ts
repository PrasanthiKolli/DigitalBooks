import { Component, OnInit } from '@angular/core';
import { Book } from '../entity/book';
import { TokenStorageService } from '../_services/token-storage.service';
import { UserService } from '../_services/user.service';


@Component({
  selector: 'app-searchform',
  templateUrl: './searchform.component.html',
  styleUrls: ['./searchform.component.css']
})
export class SearchformComponent {

  isSearchSuccess = false;
  isSearchFailed = false;
  isSubscribe=false;
  isUnSubscribe=false;
  successMessage="";
  errorMessage = "";
  content:any[]=[];
  books: any[] = [];
  search: any = {
    category: null,
    title: null,
    author: null,
    price: null,
    publisher: null
  };
  book: Book = {
    id: null,
    logo: null,
    title: null,
    authorId: null,
    authorName: null,
    publisher: null,
    category: null,
    content: null,
    price: null,
    publishedDate: null,
    active: null
  }

  constructor(private userService: UserService, private tokenService: TokenStorageService) { }
  userLoggedIn = this.tokenService.getUser() !== null;
  public Search() {
    const { category, title, author, price, publisher } = this.search;
    this.userService.search(category, title, author, price, publisher).subscribe(
      data => {
        console.log(data);
        this.isSearchSuccess = true;
        this.isSearchFailed=false;
        for (let d of data) {
          this.books.push(d);
        }
      },
      error => {
        console.error(error);
        this.isSearchFailed = true;
        this.isSearchSuccess=false;
        this.errorMessage = error.error.message


      }
    );


  }

  checksubscribe(bookId: any) {
    let subs : any[] = this.tokenService.getUser().subscriptions;
    for(let sub of subs){
      if(bookId === sub.bookId) {
        return false;
      }
    }
    return true;
  }
  checkUnSubscribe(bookId: any){
    let subs : any[] = this.tokenService.getUser().subscriptions;
    for(let sub of subs){
      if(bookId === sub.bookId && this.userService.verifyIfLessThan24Hrs(bookId)) {
        return true;
      }
    }
    return false;
  }

  public subscribeABook(bookId: any) {
    this.userService.subscribeABook(bookId, this.tokenService.getUser().id).subscribe(
      data => {
        console.log(data);
        let user = this.tokenService.getUser();
        let subs = user.subscriptions;
        subs.push({
          userId:user.id,
          bookId:bookId,
          id:data.id,
          subscriptionTime:data.subscriptionTime,
          active:true
        })
        user.subscriptions = subs;
        this.tokenService.saveUser(user);
        this.successMessage="Subscription successful!";
        this.isSubscribe=true;
        setTimeout(() => {
          this.isSubscribe=false;
          this.successMessage="";
        }, 2500);
      },
      error => {
        console.error(error);
        this.isSubscribe=false;
      }
    )
  }
  public unSubscribeBook(bookId: any) {
    let subs : any[] = this.tokenService.getUser().subscriptions;
    let subId:number;
    for(let sub of subs){
      if(bookId === sub.bookId) {
        subId = sub.id;
        this.cancelSubscription(subId);
      }
  }
}
cancelSubscription(subId:number){
  this.userService.cancelSubscription(subId, this.tokenService.getUser().id).subscribe(
    data=>{
      console.log(data);
      let user = this.tokenService.getUser();
      let subs = user.subscriptions;
      subs = subs.filter((sub: { id: number; }) => sub.id !== subId)
      user.subscriptions = subs;
      this.tokenService.saveUser(user);
      this.successMessage="Cancelled subscription successfully!";
      this.isUnSubscribe=true;
      setTimeout(() => {
        this.isUnSubscribe=false;
        this.successMessage="";
      }, 2500);
    },
    error=>{
      console.error(error);
    }
  );
}




}
