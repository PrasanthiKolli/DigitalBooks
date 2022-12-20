import { Component, OnInit } from '@angular/core';
import { Book } from '../entity/book';
import { AuthorserviceService } from '../_services/authorservice.service';

@Component({
  selector: 'app-createbook',
  templateUrl: './createbook.component.html',
  styleUrls: ['./createbook.component.css']
})
export class CreatebookComponent {

  isSuccessful = false;
  errorMessage = "";
  book : Book = {
    logo: null,
    title: null,
    publisher: null,
    category: null,
    content: null,
    price: null,
    id: null,
    authorId: null,
    authorName: null,
    publishedDate: null,
    active: null
  }

  constructor(private authorService: AuthorserviceService) { }

  createBook(){
    const{logo, title, publisher,category,content,price} = this.book;
    this.authorService.createBook(this.book).subscribe(data=> {
      this.isSuccessful = true;
    },
    error=> {
      console.error(error);
      this.errorMessage = error.error;
      this.isSuccessful = false;
    })
  }

}
