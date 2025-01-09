import React from 'react';
import {View, StyleSheet} from 'react-native';
import {Input, Button, Text} from '@rneui/themed';
import useOtp from './useFunction';
import {WINDOW_HEIGHT} from '../../../../core/utils/dimensions';

export const OtpScreen = () => {
  const {otp, inputRefs, handleOtpChange, handleConfirm, handleBackspace} =
    useOtp();

  return (
    <View style={styles.container}>
      <Text h4 style={styles.title}>
        Nhập mã OTP
      </Text>
      <View style={styles.otpContainer}>
        {otp.map((value, index) => (
          <Input
            key={index}
            value={value}
            onChangeText={text => handleOtpChange(text, index)}
            maxLength={1}
            keyboardType="numeric"
            containerStyle={styles.inputContainer}
            inputStyle={styles.input}
            ref={(e: any) => (inputRefs.current[index] = e)} // Gán ref cho từng input
            onKeyPress={e => handleBackspace(e, index)} // Bắt sự kiện phím xóa
          />
        ))}
      </View>
      <Button
        title="Xác nhận"
        onPress={handleConfirm}
        buttonStyle={styles.button}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    height: WINDOW_HEIGHT,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 20,
  },
  title: {
    marginBottom: 20,
    color:"#fd79a8"
  },
  otpContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 30,
  },
  inputContainer: {
    width: 60,
  },
  input: {
    textAlign: 'center',
    fontSize: 20,
    padding: 10,
    color: '#fd79a8',
  },
  button: {
    backgroundColor: "#fd79a8"
  },
});

export default OtpScreen;
