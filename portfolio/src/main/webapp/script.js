// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Adds a random fact to the page.
 */
function addRandomFact() {
    const facts =
        [
            'I am Romanian.', 
            'Guitar and piano are some instruments that I play.', 
            'I am really tall (180 cm).', 
            'Friends call me by my middle name.',
            'I have a dog called Zorro.',
            'I am a STEP intern at Google.',
            'I have two sisters, Sofia and Miruna.'
        ];

    // Pick a random fact.
    const fact = facts[Math.floor(Math.random() * facts.length)];

    // Add it to the page.
    const factContainer = document.getElementById('fact-container');
    factContainer.innerText = fact;
}

var slideIndex = 0;
var slides = document.getElementsByClassName("mySlides");

function showSlides() {
    if (hasSlides()) {
        hideAllSlides();
        checkLastImage();
        displayOneSlide();
        goToNextSlide();
        setTimeout(showSlides, 3000); // Change image every 3 seconds
    } else {
        alert("There are no images!");
    }
}

function hideAllSlides() {
    for (var i = 0; i < slides.length; i++) {
        slides[i].style.display = "none";  
    }
}

function checkLastImage() {
    if (slideIndex > slides.length - 1) {
        slideIndex = 0;
  }
}

function displayOneSlide() {
    slides[slideIndex].style.display = "block";
}

function goToNextSlide() {
    slideIndex++;
}

function hasSlides() {
    return !(slides === undefined || slides.length == 0);
}

/** Fetches commenta from the server and adds them to the DOM. */
function getComments() {
  /*var url = '/data?quantity=' + document.getElementById("quantity") +
            '&filter=' + document.getElementById("filter").value;*/
  var url = '/data?quantity=3&filter=' + document.getElementById("filter").value;
  console.log(url);
  fetch(url)
  .then(response => response.json()).then((comments) => {
    const commentListElement = document.getElementById("history");
    comments.forEach((comment) => {
      commentListElement.appendChild(createCommentElement(comment));
    })
  });
}

/** Creates an element that represents a comment, including its delete button. */
function createCommentElement(comment) {
  const commentElement = document.createElement('li');
  commentElement.className = 'comment';

  const textElement = document.createElement('span');
  textElement.innerText = comment.author + "  (" + comment.mood + ")"
                          + "\n" + "- " + comment.email + " -"
                          + "\n\n" + comment.text;

  const deleteButton = document.createElement('button');
  deleteButton.className = 'delete-button';

  deleteButton.innerText = 'Delete';
  deleteButton.addEventListener('click', () => {
    deleteComment(comment);
    commentElement.remove();
  });
  
  commentElement.appendChild(textElement);
  commentElement.appendChild(deleteButton);

  return commentElement;
}

function deleteComment(comment) {
  const params = new URLSearchParams();
  params.append('id', comment.id);
  fetch('/delete-data', {method: 'POST', body: params});
}

function refreshPage(){
    window.location.reload();
}

function getLogin() {
  fetch('/loginstatus')
  .then(response => response.json()).then((loginUrl) => {
      if(loginUrl === "loggedIn") {
        window.location.replace("comment.html");
      } else {
        var link = "Click me to login first".link(loginUrl);
        document.getElementById("login").innerHTML = link;
      }
  });
}

/** Creates a map and adds it to the page. */
function createMap() {
  const map = new google.maps.Map(
    document.getElementById('map'), {
      center: {lat: 54.5260, lng: 15.2551},
      zoom: 4,
      fullscreenControl: true
    });
  
  var locations = [
      {
        position: new google.maps.LatLng(42.6883, 27.7139),
        info: new google.maps.InfoWindow({content: 'Vacation in Bulgary'}),
        link: 'https://en.wikipedia.org/wiki/Golden_Sands'
      }, {
        position: new google.maps.LatLng(39.0742, 21.8243),
        info: new google.maps.InfoWindow({content: 'Vacation in Greece'}),
        link: 'https://en.wikipedia.org/wiki/Greece'
      }, {
        position: new google.maps.LatLng(47.1625, 19.5033),
        info: new google.maps.InfoWindow({content: 'Hungary, learned about their culture'}),
        link: 'https://en.wikipedia.org/wiki/Culture_of_Hungary'
      }, {
        position: new google.maps.LatLng(41.9028, 12.4964),
        info: new google.maps.InfoWindow({content: 'Rome, Italy, the architecture and history are amazing'}),
        link: 'https://en.wikipedia.org/wiki/Rome'
      }, {
        position: new google.maps.LatLng(48.8566, 2.3522),
        info: new google.maps.InfoWindow({content: 'Paris, France, great people, food, museums, views'}),
        link: 'https://en.wikipedia.org/wiki/Paris'       
      }, {
        position: new google.maps.LatLng(50.9097, 1.4044),
        info: new google.maps.InfoWindow({content: 'Southapton, United Kingdom, I study'}),
        link: 'https://en.wikipedia.org/wiki/University_of_Southampton'
      }, {
        position: new google.maps.LatLng(47.3769, 8.5417),
        info: new google.maps.InfoWindow({content: 'Zurich, Switzerland, peaceful and some breathtaking views'}),
        link: 'https://en.wikipedia.org/wiki/Z%C3%BCrich'
      }, {
        position: new google.maps.LatLng(51.5074, 0.1278),
        info: new google.maps.InfoWindow({content: 'London, United Kingdom, will come back to visit more'}),
        link: 'https://en.wikipedia.org/wiki/London'
      }, {
        position: new google.maps.LatLng(47.833660, 25.924010),
        info: new google.maps.InfoWindow({content: 'My location'}),
        link: 'https://en.wikipedia.org/wiki/R%C4%83d%C4%83u%C8%9Bi'
      }
  ];

  for (var i = 0; i < locations.length; i++) {
    const marker = new google.maps.Marker({
      position: locations[i].position,
      map: map
    });

    const infoWindow = locations[i].info;
    const infoLink = locations[i].link;

    marker.addListener('click', function() {
      infoWindow.open(map, marker);
      document.getElementById("info").innerHTML='<object class="link-style" type="text/html" data=' +
                                                infoLink + '></object>';
    });
  }
}