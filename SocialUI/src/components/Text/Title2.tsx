import {Text} from '@rneui/base';
import {TextType} from '../../core/types/TypeText/Text';

export const Title2 = (props: TextType) => {
  return (
    <Text h2 style={props.style}>
      {props.content}
    </Text>
  );
};
