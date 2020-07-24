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
  fetch('/data')
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
  textElement.innerText = comment.author +  ":\n" + comment.text;

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