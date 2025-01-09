import {Image} from '@rneui/base';
import ICONS from '../../assets/icons';
import { ImageStyle, StyleSheet } from 'react-native';
import { StyleProp } from 'react-native';
const Header = (props?: StyleProp<ImageStyle>) => {
    return (
        <Image source={ICONS.ILLIT} style={[styles.illit, props]} />
    )
}
const styles = StyleSheet.create({
    illit: {
      width: 100,
      height: 100,
      borderRadius: 50,
    },
  });
  
export default Header;