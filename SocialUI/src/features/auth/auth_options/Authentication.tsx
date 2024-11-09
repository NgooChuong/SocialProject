/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React from 'react';
import {StyleSheet, View} from 'react-native';
import {WINDOW_HEIGHT, WINDOW_WIDTH} from '../../../core/utils/dimensions';
import {CustomButton} from '../../../components/Button/Button';
import {Title2} from '../../../components/Text/Title2';
import {Image} from '@rneui/base';
import ICONS from '../../../assets/icons';
import {Title1} from '../../../components/Text/Title1';

export const Authentication = () => {
  const options = [
    {
      name: 'Login with Phone',
      icon: <Image source={ICONS.PHONE} style={styles.icon} />,
      onPress: () => console.log('Google Pressed'),
      styles: styles.button,
      stylesTitle: styles.content,
    },
    {
      name: 'Login with Google',
      icon: <Image source={ICONS.GOOGLE} style={styles.icon} />,
      onPress: () => console.log('Google Pressed'),
      styles: styles.button,
      stylesTitle: styles.content,
    },
    {
      name: 'Login with Facebook',
      icon: <Image source={ICONS.FACEBOOK} style={styles.icon} />,
      onPress: () => console.log('Facebook Pressed'),
      styles: styles.button,
      stylesTitle: styles.content,
    },
  ];

  const buttons = options.map((button, index) => {
    return (
      <CustomButton
        key={index}
        icon={button.icon}
        name={button.name}
        onPress={button.onPress}
        style={button.styles}
        titleStyle={button.stylesTitle}
      />
    );
  });

  return (
    <View style={styles.sectionContainer}>
      <Image source={ICONS.ILLIT} style={styles.illit} />
      <Title1 content={'COFFEE MOKA'} style={styles.title} />
      {buttons}

    </View>
  );
};

const styles = StyleSheet.create({
  sectionContainer: {
    height: WINDOW_HEIGHT,
    justifyContent: 'center',
    alignItems: 'center',
  },
  listItem: {
    height: WINDOW_HEIGHT / 2,
  },
  buttonGroup: {
    height: 100,
    display: 'flex',
    flexDirection: 'column',
  },
  button: {
    width: WINDOW_WIDTH / 1.5,
    margin: 5,
    borderRadius: 16,
    backgroundColor: 'white',
    borderStyle: 'solid',
    borderWidth: 1,
    borderColor: 'gray',
  },
  content: {
    color: 'black',
  },
  icon: {
    width: 20,
    height: 20,
    marginRight: 10,
  },
  illit: {
    width: 100,
    height: 100,
    borderRadius: 50,
  },
  title: {
    textAlign: 'center',
    margin: 10,
  },
});
