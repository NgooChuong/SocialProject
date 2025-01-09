import {createNativeStackNavigator} from '@react-navigation/native-stack';
import ListPost from '../features/ListPost/ListPostScreen';
import { NAVIGATION_ROUTING_KEY } from '../core/enums';


const RootStack = createNativeStackNavigator();

const MyRootStack = () => {
  return (
    <RootStack.Navigator initialRouteName={NAVIGATION_ROUTING_KEY.LISTJOB_SCREEN}>
      <RootStack.Screen
        name= {NAVIGATION_ROUTING_KEY.LISTJOB_SCREEN}
        component={ListPost}
        options={{headerShown: false}}
      />
    </RootStack.Navigator>
  );
};
export default MyRootStack;
