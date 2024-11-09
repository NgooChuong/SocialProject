import {Text} from '@rneui/base';
import {TextType} from '../../core/types/TypeText/Text';

export const Title1 = (props: TextType) => {
  return (
    <Text h1 style={props.style}>
      {props.content}
    </Text>
  );
};
