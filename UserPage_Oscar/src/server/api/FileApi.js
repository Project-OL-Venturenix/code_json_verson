const fs = require('fs');
const path = require('path');
const mkdirp = require('mkdirp');

let question;
module.exports = {

  saveFile(file, data, questionId, callback) {
    mkdirp(path.dirname(file), (err) => {
      if (err) return callback(err);

      const jsonData = data;
      const jsonDataWithoutClosingBrace = jsonData.replace(/\}\s*$/, '');
      //add mainmethod
      const jsonFile = path.join(__dirname, '../templates', `Question${questionId}.json`);
      question = questionId;
      fs.readFile(jsonFile, 'utf8', (err, data) => {
        if (err) throw err;
        let mainMethodData;
        try {
          mainMethodData = JSON.parse(data);
        } catch (parseError) {
          throw new Error('Error parsing JSON data');
        }
        if (!mainMethodData || typeof mainMethodData !== 'object') {
          throw new Error('Invalid JSON data format: Not an object');
        }
        const { mainMethod } = mainMethodData;
        if (typeof mainMethod !== 'string') {
          throw new Error('Invalid JSON data format: Missing or invalid string properties');
        }

        const javaClass = `${jsonDataWithoutClosingBrace}\n\n${mainMethod}\n\n`;

        fs.writeFile(file, javaClass, 'utf8', (err2) => {
          if (err2) throw err2;
          callback();
        });
      });
    });
  },
  //question part 
  startConvertJsonToJava( questionId, callback) {
    const jsonFile = path.join(__dirname, '../templates', `Question${questionId}.json`);
    fs.readFile(jsonFile, 'utf8', (err, data) => {
      if (err) throw err;
      let jsonData;
      try {
        jsonData = JSON.parse(data);
      } catch (parseError) {
        throw new Error('Error parsing JSON data');
      }
      if (!jsonData || typeof jsonData !== 'object') {
        throw new Error('Invalid JSON data format: Not an object');
      }
      const { classDeclaration, code } = jsonData;
      if (typeof classDeclaration !== 'string' || typeof code !== 'string') {
        throw new Error('Invalid JSON data format: Missing or invalid string properties');
      }
      const javaCode = `${classDeclaration}\n${code}\n`;
      callback(javaCode);
    });
  },

  getFile(lang, questionId, callback) {
    let file = '';
    const language = lang.toLowerCase();
    if (language === 'java') {
      file = path.join(__dirname, '../templates', `Question${questionId}.java`);
      if (!fs.existsSync(file)) {
        fs.writeFileSync(file, '');
      }
      this.startConvertJsonToJava( questionId, (javaCode) => {
        fs.writeFile(file, javaCode, (err) => {
          if (err) throw err;
          console.log(`Question${questionId}.java file created with Java code.`);
          fs.readFile(file, (err, data) => {
            if (err) throw err;
            console.log('data.toString() : ' + data.toString());
            callback(data.toString());
          });
        });
      });
    } else if (language === 'c') {
      file = path.join(__dirname, '../templates', 'Hello.c');
    } else if (language === 'c++') {
      file = path.join(__dirname, '../templates', 'Hello.cpp');
    } else if (language === 'javascript') {
      file = path.join(__dirname, '../templates', 'Hello.js');
    } else if (language === 'python') {
      file = path.join(__dirname, '../templates', 'Hello.py');
    } else {
      callback('');
      return;
    }
  },
};