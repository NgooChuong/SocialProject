import {Button, Icon} from '@rneui/base';
import {ReactElement} from 'react';
import {StyleProp, TextStyle, ViewStyle} from 'react-native';

type Button = {
  name: string;
  icon?: string | ReactElement;
  onPress?: () => void;
  style?: StyleProp<ViewStyle>;
  titleStyle?: StyleProp<TextStyle>;
};
// eslint-disable-next-line @typescript-eslint/explicit-module-boundary-types
export const CustomButton = (props: Button) => {
  return (
    <Button
      icon={
        typeof props.icon === 'string' ? (
          <Icon name={props.icon} size={24} />
        ) : (
          props.icon
        )
      }
      title={props.name}
      buttonStyle={props.style}
      onPress={props.onPress}
      titleStyle={props.titleStyle}
    />
  );
};
