import { Injectable } from '@angular/core';
import { Book } from '../entity/book';

@Injectable({
  providedIn: 'root'
})
export class BookStoreService {

  book : Book = {
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

  constructor() { }

  setBook(book:any){
    this.book = book;
  }

  getBook(){
    return this.book;
  }
}


