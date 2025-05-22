import { Component, computed, inject, OnInit } from '@angular/core';
import { AdminService } from '../../services/admin.service';
import { User } from '../../../auth/interfaces/user.interface';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { MessageService } from 'primeng/api';
import { DropdownModule } from 'primeng/dropdown';
import { AuthService } from '../../../auth/services/auth.service';
import { Toast } from 'primeng/toast';

@Component({
  selector: 'app-users-page',
  imports: [
    TableModule,
    CommonModule,
    FormsModule,
    ButtonModule,
    CommonModule,
    DropdownModule,
    Toast,
  ],
  providers: [MessageService],
  templateUrl: './users-page.component.html',
  styles: ``,
})
export class UsersPageComponent implements OnInit {
  private readonly _adminService = inject(AdminService);
  private readonly _authService = inject(AuthService);
  private readonly _messageService = inject(MessageService);

  users: User[] = [];
  usersFiltered: User[] = this.users;

  user = computed(() => this._authService.user());

  roles = [
    { name: 'Administrador', value: 'ROLE_ADMIN' },
    { name: 'Usuario', value: 'ROLE_USER' },
  ];
  state = [
    { name: 'Habilitado', value: true },
    { name: 'Deshabilitado', value: false },
  ];
  stateSelected: boolean = this.state[0].value;

  query = '';

  ngOnInit(): void {
    this._adminService.getAllUsers().subscribe((users: User[]) => {
      this.users = users;
    });
  }

  updateUser(user: User) {
    this._adminService.updateUser(user.id, user).subscribe(
      () => {
        this._messageService.add({
          severity: 'success',
          summary: 'Usuario actualizado',
        });
      },
      () => {
        this._messageService.add({
          severity: 'error',
          summary: 'Error al actualizar el usuario',
        });
      }
    );
  }

  searchUsers(event: Event) {
    this.usersFiltered = this.users.filter((user) =>
      user.username.toLowerCase().includes(this.query.toLowerCase())
    );
  }
}
