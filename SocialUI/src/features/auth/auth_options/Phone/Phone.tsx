import React from 'react';
import {StyleSheet, View} from 'react-native';
import {Input} from '@rneui/themed';
import {WINDOW_HEIGHT} from '../../../../core/utils/dimensions';
import ICONS from '../../../../assets/icons';
import {Image, Text} from '@rneui/base';
import Header from '../../../../components/Header/header';
import {Title3} from '../../../../components/Text/Title3';
const PhoneScreen = () => {
  return (
    <View style={styles.containers}>
      <View style={{width: '100%', alignItems: 'center'}}>
        <Header />
        <Title3 style={styles.title} content={'Đăng nhập'} />
      </View>
      <View style={styles.phone}>
        <Text>Số điện thoại</Text>
        <Text style={{color: 'red', fontSize: 14}}>{'*'}</Text>
      </View>
        <Input
          leftIcon={<Image source={ICONS.PHONE} style={styles.icon} />}
          placeholder="Nhập số điện thoại của bạn"
          inputContainerStyle={styles.input}
          keyboardType="numeric"
          maxLength={10}
        />
    </View>
  );
};
const styles = StyleSheet.create({
  containers: {
    justifyContent: 'center',
    height: WINDOW_HEIGHT - 100,
  },
  input: {
    padding: 5,
    margin: 10,
  },
  icon: {
    width: 20,
    height: 20,
  },
  phone: {
    display: 'flex',
    flexDirection: 'row',
    marginLeft: 20,
    marginTop: 20,
  },
  title: {
    textAlign: 'center',
    color: '#fd79a8',
  },
});
export default PhoneScreen;
