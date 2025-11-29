import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginPage } from './pages/login.page';
import { DashboardPage } from './pages/dashboard.page';
import { ProjectsPage } from './pages/projects.page';
import { ProjectDetailPage } from './pages/project-detail.page';
import { TasksPage } from './pages/tasks.page';
import { UsersPage } from './pages/users.page';
import { AuthGuard } from './guards/auth.guard';

const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginPage },
  { path: 'dashboard', component: DashboardPage, canActivate: [AuthGuard] },
  { path: 'projects', component: ProjectsPage, canActivate: [AuthGuard] },
  { path: 'projects/:id', component: ProjectDetailPage, canActivate: [AuthGuard] },
  { path: 'tasks', component: TasksPage, canActivate: [AuthGuard] },
  { path: 'users', component: UsersPage, canActivate: [AuthGuard] },
  { path: '**', redirectTo: 'login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
