import {
  AbstractControl,
  FormGroup,
  ValidationErrors,
  ValidatorFn,
} from '@angular/forms';

export function isFieldEquals(field1: string, field2: string): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const formGroup = control as FormGroup;
    const value1 = formGroup.get(field1)?.value;
    const value2 = formGroup.get(field2)?.value;

    if (value1 !== value2) {
      formGroup.get(field2)?.setErrors({ fieldsNotEqual: true });
      return { fieldsNotEqual: true };
    }
    return null;
  };
}

export function message(field: string) {
  return (formGroup: FormGroup) => {
    const errors = formGroup.get(field)?.errors;
    if (!errors) return null;

    for (const key in errors) {
      switch (key) {
        case 'required':
          return `Este campo es requerido`;
        case 'email':
          return `Correo electrónico inválido`;
        case 'minlength':
          return `Mínimo ${errors[key].requiredLength} caracteres`;
        case 'fieldsNotEqual':
          return `Las contraseñas no coinciden`;
        case 'emailTaken':
          return `El correo electrónico ya está en uso`;
        default:
          return `Campo inválido`;
      }
    }
    return '';
  };
}
