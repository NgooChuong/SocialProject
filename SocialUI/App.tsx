/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React from 'react';
import MainNavigation from './src/navigation/MainNavigation';
import { configure } from 'mobx';
import { unstable_batchedUpdates } from 'react-native';
configure({
  enforceActions: 'observed',
  reactionScheduler: unstable_batchedUpdates
});

const App = () => {
  return <MainNavigation />;
};

export default App;
