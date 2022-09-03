/* eslint-disable no-self-assign */

(function () {
  console.log('');
})();

window.onload = function () {
  // 顶部
  const pull_rightLength = document.getElementsByClassName('pull-right').length;
  for (let index = 0; index < pull_rightLength; index++)
    document.getElementsByClassName('pull-right')[0].remove();

  // Copyright
  document.getElementsByClassName('footer-modification')[0].remove();
  document.getElementsByClassName('page-footer')[0].style.textAlign = 'center';
  document.getElementsByClassName('copyright')[0].innerHTML = document
    .getElementsByClassName('copyright')[0]
    .innerHTML.replace(' all right reserved，powered by Gitbook', '');

  // 上下页
  const divID357913 = document.createElement('div');
  divID357913.id = 'ID357913';
  divID357913.style.paddingBottom = '26px';

  document
    .getElementsByTagName('footer')[0]
    .parentNode.insertBefore(
      divID357913,
      document.getElementsByTagName('footer')[0]
    );
  const navigation_prev = document.getElementsByClassName('navigation-prev');
  const navigation_next = document.getElementsByClassName('navigation-next');

  if (navigation_prev.length) {
    navigation_prev[0].href = navigation_prev[0].href;
    divID357913.appendChild(navigation_prev[0]);
  }
  if (navigation_next.length) {
    navigation_next[0].href = navigation_next[0].href;
    divID357913.appendChild(navigation_next[0]);
  }

  // <a />
  const AObjectS = document.getElementsByTagName('a');
  for (let index = 0; index < AObjectS.length; index++) {
    if (AObjectS[index].href.indexOf('http') >= 0)
      AObjectS[index].href = AObjectS[index].href;
    else
      AObjectS[index].href =
        window.origin + AObjectS[index].href.replace(/..\//g, '');
  }
};