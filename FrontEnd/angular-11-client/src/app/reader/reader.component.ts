import { Component, OnInit } from '@angular/core';
import { Book } from '../entity/book';
import { ReaderService } from '../_services/reader.service';
import { TokenStorageService } from '../_services/token-storage.service';
import { UserService } from '../_services/user.service';

@Component({
  selector: 'app-reader',
  templateUrl: './reader.component.html',
  styleUrls: ['./reader.component.css']
})
export class ReaderComponent implements OnInit {
  book:Book={
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
  books:any[]=[];
  user: any = {
    id: null,
    username: null,
    emailid: null,
    phoneNumber: null,
    roles :null,
    subscriptions:null
  };

  constructor(private readerService:ReaderService, private tokenService:TokenStorageService,private userService:UserService) { }

  ngOnInit(): void {
      this.user=this.tokenService.getUser();
      this.readerService.getAllSubscribedBooks().subscribe(
        data =>{
          for(let b of data){
            console.log(data);
            this.book=b;
            this.books.push(this.book);
          }
        },
        error => {
          console.error(error);
        }
      );
    }

    public unSubscribeBook(bookId: any) {
      let subs : any[] = this.tokenService.getUser().subscriptions;
      let subId:number;
      for(let sub of subs){
        if(bookId === sub.bookId) {
          subId = sub.id;
          this.cancelSubscription(subId);
          this.books.filter(b => {
            if(b.id !== bookId){
            return true}
          return false})
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
        //this.tokenService.reloadUser(this.tokenService.getUser().id);
      },
      error=>{
        console.error(error);
      }
    );
  }
}
  
  


