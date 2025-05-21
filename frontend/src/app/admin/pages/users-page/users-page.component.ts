import { Component, inject, OnInit } from '@angular/core';
import { AdminService } from '../../services/admin.service';
import { User } from '../../../auth/interfaces/user.interface';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { Toast } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { DropdownModule } from 'primeng/dropdown';

@Component({
  selector: 'app-users-page',
  imports: [
    TableModule,
    CommonModule,
    FormsModule,
    ButtonModule,
    Toast,
    CommonModule,
    DropdownModule,
  ],
  providers: [MessageService],

  templateUrl: './users-page.component.html',
  styles: ``,
})
export class UsersPageComponent implements OnInit {
  private readonly _adminService = inject(AdminService);

  users: User[] = [];
  usersFiltered: User[] = this.users;

  roles = ['ADMIN', 'USER'];

  query = '';

  ngOnInit(): void {
    this._adminService.getAllUsers().subscribe((users: User[]) => {
      this.users = users;
    });
  }

  onCreateAdmin() {}

  updateUser(user: User) {
    // this._adminService.updateUser(user.id, user).subscribe()
  }

  searchUsers(event: Event) {
    this.usersFiltered = this.users.filter((user) =>
      user.username.toLowerCase().includes(this.query.toLowerCase())
    );
  }

  onDeleteUser(userId: string) {}
}
