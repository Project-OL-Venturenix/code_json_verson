import HttpHelper from './HttpHelper';

class CompilerApi {
  static requestHeaders() {
    return { 'Content-Type': 'application/json' };
  }

  static getTask(lang, questionId) {
    return HttpHelper.fetch(
      `${process.env.API_URL}/api/file/${lang}/${questionId}`,
      'GET',
      this.requestHeaders(),
      null,
    );
  }

  static run(answer, questionId) {
    return HttpHelper.fetch(
      `${process.env.API_URL}/api/run/${questionId}`,
      'POST',
      this.requestHeaders(),
      JSON.stringify(answer),
    );
  }
}

export default CompilerApi;
