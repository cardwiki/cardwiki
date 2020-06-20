import { Component, Input } from '@angular/core';
import { CommentSimple } from 'src/app/dtos/commentSimple';
import { CommentService } from 'src/app/services/comment.service';
import { Observable } from 'rxjs';
import { AuthService } from 'src/app/services/auth.service';
import { NotificationService } from 'src/app/services/notification.service';

@Component({
  selector: 'app-comment-list',
  templateUrl: './comment-list.component.html',
  styleUrls: ['./comment-list.component.css']
})
export class CommentListComponent {

  @Input() comments: CommentSimple[]

  activeUserName$: Observable<string>
  editingCommentId = -1 // id of the comment currently edited or -1

  constructor(private commentService: CommentService, private authService: AuthService, private notificationService: NotificationService) {
    this.activeUserName$ = this.authService.userName$
  }

  editComment(id: number) {
    this.editingCommentId = id
  }

  editCommentSubmit(message: string) {
    this.commentService.editComment(this.editingCommentId, message)
      .subscribe(comment => {
        this.notificationService.success('Edited comment')
        this.comments = this.comments
        const index = this.comments.findIndex(c => c.id === comment.id)
        this.comments[index] = comment
        this.editingCommentId = -1
      })
  }

  deleteComment(id: number) {
    this.commentService.deleteComment(id)
      .subscribe(() => {
        const index = this.comments.findIndex(c => c.id === id)
        this.comments.splice(index, 1)
        this.notificationService.success('Deleted comment')
      })
  }

}
