import React from 'react';
import PropTypes from 'prop-types';
import { FormControl } from 'react-bootstrap';

class OutputBox extends React.Component {
  constructor(props, context) {
    super(props, context);
    this.state = {};
  }

  render() {
    // if (this.props.show) {
    const { show, message, executionTime, question } = this.props;

    // Regular expression to extract the counter value
    const regex = /Test Case Result: (\d+) \/ 10/;
    const match = message.match(regex);
    const counter = match ? match[1] : 'N/A'; // Default to 'N/A' if counter not found
    let messageText;

    if (counter !== 'N/A') {
        if (counter == 10) {
          if (executionTime <= question.bonusRuntime) {
            messageText = ` Test Case Pass: ${counter} / 10 \n Congratulations! You've passed all the test cases with flying colors! \n You've earned 3 marks! \n Furthermore, your algorithm met the required runtime. \n Outstanding job! You've earned an additional bonus mark!` ;
          } else {
            messageText = ` Test Case Pass: ${counter} / 10 \n You have successfully passed all the test cases! \n You've earned 3 marks! \n However, your algorithm did not meet the required runtime. \n Please work on optimizing it.` ;
          }
        } else {
          messageText = `Test Case Pass: ${counter} / 10 \n Unfortunately, you have not passed the required test cases. \n As a result, you did not receive any marks. \n Please consider revising and trying again.` ;
        }
    }

    // console.log(question.targetcompletetime);
    console.log('counter:', typeof (counter));
    console.log('counter:', counter);

    localStorage.setItem('counter', counter);
    if (show) {
      return (
          <FormControl
              name="code"
              type="textarea"
              componentClass="textarea"
              rows="8"
              readOnly
              // value={this.props.message}
              value={messageText}
              style={{ fontSize: '20px' }}
          />
      );
    }

    return (
        <FormControl
            name="code"
            type="textarea"
            componentClass="textarea"
            rows="8"
            readOnly
            value=""
        />
    );
  }
}

OutputBox.propTypes = {
  show: PropTypes.bool.isRequired,
  message: PropTypes.string.isRequired,
};

export default OutputBox;