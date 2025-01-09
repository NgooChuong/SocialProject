import { makeAutoObservable } from "mobx";

const AuthenStore =  {
  isAuthentication: false,

  setIsAuthentication(value: boolean) {
    this.isAuthentication = value;
  },

};

makeAutoObservable(AuthenStore);

export default AuthenStore;
