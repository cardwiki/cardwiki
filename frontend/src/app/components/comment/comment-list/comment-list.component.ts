import { Component, Input, ViewChild } from '@angular/core';
import { CommentSimple } from 'src/app/dtos/commentSimple';
import { CommentService } from 'src/app/services/comment.service';
import { Observable } from 'rxjs';
import { AuthService } from 'src/app/services/auth.service';
import { NotificationService } from 'src/app/services/notification.service';
import { map } from 'rxjs/operators';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ConfirmModalComponent } from '../../utils/confirm-modal/confirm-modal.component';
import { CommentFormComponent } from '../comment-form/comment-form.component';

@Component({
  selector: 'app-comment-list',
  templateUrl: './comment-list.component.html',
  styleUrls: ['./comment-list.component.css']
})
export class CommentListComponent {

  @Input() comments: CommentSimple[]

  activeUserName$: Observable<string>
  isAdmin$: Observable<boolean>
  editingCommentId = -1 // id of the comment currently edited or -1

  @ViewChild('commentForm') private commentForm: CommentFormComponent

  constructor(private commentService: CommentService, private authService: AuthService, private notificationService: NotificationService,
              private modalService: NgbModal) {
    this.activeUserName$ = this.authService.userName$
    this.isAdmin$ = this.authService.userRoles$.pipe(map(roles => roles.includes('ADMIN')))
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
        this.commentForm.reset()
      })
  }

  deleteComment(id: number) {
    const confirmationModal = this.modalService.open(ConfirmModalComponent)
    confirmationModal.componentInstance.title = 'Delete Comment'
    confirmationModal.componentInstance.message = 'Are you sure you want to delete this comment?'
    confirmationModal.componentInstance.action = 'Delete'

    confirmationModal.result.then(() => {
      this.commentService.deleteComment(id)
        .subscribe(() => {
          const index = this.comments.findIndex(c => c.id === id)
          this.comments.splice(index, 1)
          this.notificationService.success('Deleted comment')
        })
    }).catch(err => console.log('Comment deletion cancelled', err))
  }

}
