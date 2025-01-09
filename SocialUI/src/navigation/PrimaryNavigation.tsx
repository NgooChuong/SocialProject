import {createNativeStackNavigator} from '@react-navigation/native-stack';
import {Authentication} from '../features/auth/auth_options/Authentication';
import OtpScreen from '../features/auth/auth_options/otp/otp';
import PhoneScreen from '../features/auth/auth_options/Phone/Phone';
import { NAVIGATION_ROUTING_KEY } from '../core/enums';

const PrimaryStack = createNativeStackNavigator();

const MyPrimaryStack = () => {
  return (
    <PrimaryStack.Navigator initialRouteName={NAVIGATION_ROUTING_KEY.LOGIN_SCREEN}>
      <PrimaryStack.Screen
        name = {NAVIGATION_ROUTING_KEY.LOGIN_SCREEN}
        component={Authentication}
        options={{headerShown: false}}
      />
      <PrimaryStack.Screen
        name={NAVIGATION_ROUTING_KEY.PHONE_SCREEN}
        component={PhoneScreen}
        options={{headerShown: false}}
      />
      <PrimaryStack.Screen
        name={NAVIGATION_ROUTING_KEY.OTP_SCREEN}
        component={OtpScreen}
        options={{headerShown: false}}
      />
    </PrimaryStack.Navigator>
  );
};
export default MyPrimaryStack;
