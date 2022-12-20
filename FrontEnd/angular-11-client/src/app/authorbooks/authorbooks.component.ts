import { Component, OnInit } from '@angular/core';
import { AuthorserviceService } from '../_services/authorservice.service';
import { BookStoreService } from '../_services/book-store.service';
import { TokenStorageService } from '../_services/token-storage.service';

@Component({
  selector: 'app-authorbooks',
  templateUrl: './authorbooks.component.html',
  styleUrls: ['./authorbooks.component.css']
})
export class AuthorbooksComponent implements OnInit {

  user: any = {
    id: null,
    username: null,
    emailid: null,
    phoneNumber: null,
    roles :null,
    subscriptions:null
  };

  public books:any[] = []

  public book : any = {
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
  isBooksAvailable = false;
  showSuccess=false;
  successMessage="";
  showErrorMessage=false;
  errorMessage="";

  constructor(private authorService: AuthorserviceService, private tokenStorageService: TokenStorageService, private bookService: BookStoreService) { }

  ngOnInit(): void {
    this.user = this.tokenStorageService.getUser();
    this.authorService.getBooksCreatedByAuthor(this.user.id).subscribe(
      data  => {
        for(let b of data){
          this.book = b;
          this.books.push(this.book);
        }
      },
      error => {
        console.error(error);
      }
    );

  }

  onClick(book : any) : void {
    this.bookService.setBook(book);
  }

  onUpdate(book : any) : void {
    this.bookService.setBook(book);
  }
  block(bookId : any,book:any):void{
    console.log("Blocking the book with Id : "+bookId);
    this.blockBook(bookId,"yes","blocked",book);
  }
  unBlock(bookId:any,book:any):void{
    console.log("UnBlocking the book with Id : "+bookId);
    this.blockBook(bookId,"no","unblocked",book);
  }
  blockBook(bookId:any, block:any, message:String,book:any){
    this.authorService.blockBook(bookId, block,book).subscribe(
      data=>{
        console.log("book updated");
        this.modifyBooksDataOfAuthor(bookId, block);
        this.successMessage="Book "+message+" successfully!";
        this.showSuccess=true;
        setTimeout(() => {
          this.showSuccess=false;
          this.successMessage="";
        }, 2500);
      },
      error=>{
        console.error(error);
        this.errorMessage="Failed to "+message.substring(0,message.length-2)+" book!";
        this.showErrorMessage=true;
        setTimeout(() => {
          this.showErrorMessage=false;
          this.errorMessage="";
        }, 2500);
      }
    );
  }
  modifyBooksDataOfAuthor(bookId: any, block: any) {
    let books = this.books;
    let active = true;
    if(block === 'yes')
      active = false;
    else
      active = true;
    books.map(b=>{
      if(b.id === bookId){
        b.active = active;
      }
    })
  }


}
