import {GoogleSignin} from '@react-native-google-signin/google-signin';
import auth from '@react-native-firebase/auth';
import { NAVIGATION_ROUTING_KEY } from '../../../core/enums';
import AuthenStore from '../../../stores/authen.store';
GoogleSignin.configure({
  webClientId:
    '419369008319-2o62a2aakrnj8add3k5p43a04ip64sse.apps.googleusercontent.com',
});

const useAuthen = (navigation:any) => {
  // function onAuthStateChanged(user: any) {
  //   ('auth changed', user);
  //   if (user) {
  //     navigation.navigate('OTP');
  //   }
  // }

  // useEffect(() => {
  //   const subscriber = auth().onAuthStateChanged(onAuthStateChanged);
  //   return subscriber; // unsubscribe on unmount
  // }, []);
  const handleGoogleLogin = async () => {
    try {
      await GoogleSignin.hasPlayServices({showPlayServicesUpdateDialog: true});
      await GoogleSignin.signOut();
      const userInfo = await GoogleSignin.signIn();
      const idToken = userInfo.data?.idToken; // Truy cập trực tiếp idToken
      if (!idToken) {
        throw new Error('Failed to get idToken from Google sign-in.');
      }
      const googleCredential = auth.GoogleAuthProvider.credential(idToken);
      const res = await auth().signInWithCredential(googleCredential);
      if(res) AuthenStore.setIsAuthentication(true);
    } catch (error) {
      console.error('Error during Google sign-in:', error);
    }
  };
  const handlePhoneLogin = () => {
    navigation.navigate(NAVIGATION_ROUTING_KEY.PHONE_SCREEN);
  }

  return {
    handleGoogleLogin,
    handlePhoneLogin
  };
};
export default useAuthen;
