/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React from 'react';
import {Authentication} from './src/features/auth/auth_options/Authentication';
import {SafeAreaView} from 'react-native';

function App() {
  return (
    <SafeAreaView>
      <Authentication />
    </SafeAreaView>
  );
}

export default App;
