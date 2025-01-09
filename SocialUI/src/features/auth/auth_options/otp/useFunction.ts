import {useState, useRef} from 'react';
import {TextInput} from 'react-native';

const OTPInput = () => {
  const [otp, setOtp] = useState(['', '', '', '']);
  const inputRefs = useRef<TextInput[]>([]); // Khai báo mảng refs cho từng TextInput

  // Hàm xử lý khi nhập OTP
  const handleOtpChange = (text: string, index: number) => {
    const newOtp = [...otp];
    newOtp[index] = text;
    setOtp(newOtp);
    if (text && index < otp.length - 1) {
      inputRefs.current[index + 1]?.focus();
    }
  };

  const handleConfirm = () => {
    const otpCode = otp.join('');
    console.log('OTP đã nhập:', otpCode);
  };
  
  const handleBackspace = (e: any, index: number) => {
    if (e.nativeEvent.key === 'Backspace' && !otp[index]) {
      if (index > 0) {
        inputRefs.current[index - 1]?.focus();
      }
    }
  };
  return {
    otp,
    inputRefs,
    handleOtpChange,
    handleConfirm,
    handleBackspace,
  };
};

export default OTPInput;
