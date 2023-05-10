const foundedText = prompt('Введите текст, который нужно ожидать. (Уведомление придёт, когда он появится на сайте). Оставьте пустым, чтобы ожидать любое изменение');

if (foundedText.length === 0) {
}
let url = prompt('Введите ссылку на сайт. Оставьте пустым, если это сайт, на котором вы находитесь сейчас');
if (url.length === 0) {
    url = window.location.href
}

let permission = await Notification.requestPermission();
const notification = new Notification('Вот такое вылезет, когда что-то изменится', {
    icon: 'https://i.imgur.com/q8zP8U1.png'
});
setTimeout(() => notification.close(), 3 * 1000);

notification.onclick = () => {
    notification.close();
}

win = window.open(url);
timer = setInterval(() => {
    if (win.document.getElementsByClassName('article-body').item(0).textContent.includes('9 avril')) {
        console.log('Что-то обновилось, проверь!');
        const alert = new Notification('Что-то обновилось, проверь!');
        alert.onclick = () => {
            window.parent.focus();
        };
    }
    win.location.href = url
}, 10000);


function myFunction() {
    let textContent = ""
    let win = window.open(url);
    timer = setInterval(() => {
        if (win.document.getElementsByClassName('article-body').item(0).textContent === textContent) {
            textContent = win.document.getElementsByClassName('article-body').item(0).textContent
            console.log('Что-то обновилось, проверь!');
        }
        win.location.href = url
    }, 10000);
}
