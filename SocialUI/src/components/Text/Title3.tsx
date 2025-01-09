import {Text} from '@rneui/base';
import {TextType} from '../../core/types/TypeText/Text';

export const Title3 = (props: TextType) => {
  return (
    <Text h3 style={props.style}>
      {props.content}
    </Text>
  );
};
