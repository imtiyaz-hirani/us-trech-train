import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Employee } from 'src/app/models/employee.model';
import { Manager } from 'src/app/models/manager.model';
import { UserService } from 'src/app/services/user.service';
import { managers} from '../../data/data';
@Component({
  selector: 'app-sign-up',
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.css']
})
export class SignUpComponent implements OnInit {
  signUpForm: FormGroup;
  jobTitles: string[] = ['Developer', 'Product Owner', 'Scrum Master','DevOps Engineer','Tester'];
  managers: Manager[] = managers;
 employee: Employee;
  msg: string ='';
  constructor(private userService: UserService, private router: Router) { }

  ngOnInit(): void {
    this.signUpForm = new FormGroup({
        name: new FormControl('', [Validators.required, Validators.pattern(/^[a-zA-Z ]+$/)]),
        jobTitle: new FormControl('', Validators.required),
        manager: new FormControl('', Validators.required),
        email: new FormControl('', [Validators.required,Validators.pattern(/^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/)]),
        password: new FormControl('',[Validators.required,Validators.minLength(5),Validators.pattern(/^[a-zA-Z0-9 _#]+$/)]),
        repassword: new FormControl('',Validators.required)
    });
  }

  onFormSubmit(){
     /* 1. Check if Password Match */
    if(! (this.signUpForm.value.password === this.signUpForm.value.repassword)) {
        this.msg = 'Passwords do not Match';
        return;
    }
     /* 2. call API and post this data */
     this.employee = {
      name: this.signUpForm.value.name,
      jobTitle: this.signUpForm.value.jobTitle,
      email: this.signUpForm.value.email,
      password: this.signUpForm.value.password,
      managerId: this.signUpForm.value.manager
     };

     this.userService.signUp(this.employee).subscribe({
      next: (data)=>{
          this.router.navigateByUrl('/');
      },
      error: (error)=>{
        this.msg='Sign Up not possible at this time';
      }
     });
  }

}
