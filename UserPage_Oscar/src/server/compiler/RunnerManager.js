const path = require('path');
const FileApi = require('../api/FileApi');
const CRunner = require('./CRunner');
const CppRunner = require('./CppRunner');
const JavaRunner = require('./JavaRunner');
const JavaScriptRunner = require('./JavaScriptRunner');
const PythonRunner = require('./PythonRunner');

function Factory() {
  this.createRunner = function createRunner(lang, questionId) {
    let runner;

    if (lang === 'c') {
      runner = new CRunner();
    } else if (lang === 'c++') {
      runner = new CppRunner();
    } else if (lang === 'java') {
      runner = new JavaRunner(questionId);
    } else if (lang === 'javascript') {
      runner = new JavaScriptRunner();
    } else if (lang === 'python') {
      runner = new PythonRunner();
    }

    return runner;
  };
}

module.exports = {
  run(lang, code, res, questionId) {
    const factory = new Factory(questionId);
    const runner = factory.createRunner(lang.toLowerCase(),questionId);

    const directory = path.join(__dirname, 'temp');
    const file = path.join(directory, runner.defaultFile(questionId));//question file

    const filename = path.parse(file).name; // main
    const extension = path.parse(file).ext; // .java
    console.log(`frontend_Lucas_filename: ${filename}`);
    console.log(`frontend_Lucas_extension: ${extension}`);

    console.log('before saveFile');
    FileApi.saveFile(file, code, questionId,() => {
      runner.run(file, directory, filename, extension, (status, message) => {
        const result = {
          status,
          message,
        };
        // console.log('before res.end(JSON.stringify(result))')
        res.end(JSON.stringify(result));
      });
    });
    console.log('after saveFile');

  },
};
