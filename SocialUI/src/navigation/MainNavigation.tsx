import React, {useMemo} from 'react';
import {NavigationContainer} from '@react-navigation/native';
import MyPrimaryStack from './PrimaryNavigation';
import MyRootStack from './RootNavigation';
import AuthenStore from '../stores/authen.store';
import {observer} from 'mobx-react-lite';

const MainNavigation = observer(() => {
  const {isAuthentication} = AuthenStore;
  const renderNavigation = useMemo(() => {
    console.log('renderNavigation', AuthenStore.isAuthentication);
    return isAuthentication ? <MyRootStack /> : <MyPrimaryStack />;
  }, [isAuthentication]);
  return <NavigationContainer>{renderNavigation}</NavigationContainer>;
});
export default MainNavigation;
