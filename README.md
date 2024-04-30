# User REST service

#### Total spend time: [![wakatime](https://wakatime.com/badge/user/b33e0124-90c1-44a9-95a8-0f09b324ad70/project/f9f93ef8-2b3c-471c-a80c-b75b6b971c26.svg)](https://wakatime.com/badge/user/b33e0124-90c1-44a9-95a8-0f09b324ad70/project/f9f93ef8-2b3c-471c-a80c-b75b6b971c26)

## What is the project about?

Implementation of a practical test assignment from "Clear Solutions".

## What is the purpose of this project?

The **purpose** of the project is ***implementing a simple User REST service*** with 5 main operations (GET, PUT, POST,
PATCH, DELETE) according to the best practices.

## Getting Started

For running the app you need to download the latest version from the **main** branch. As there is an embedded database (
H2) you needn't to run additional server for testing how application works.

Follow the next chapters.

### Installation Instruction

#### How to download project on my local machine?

For downloading the project locally you can use two variants:

1. Download the ZIP archive from the repository page.

   The method is easy, the next steps helps you:

    1. Find the button `Code` and press it.
    2. Find the button `Download ZIP` and press it. The downloading must start.
    3. Unzip the archive in soe directory and run the IDEA in this directory.

   Project has been installed.


2. Use the `Git` for downloading the repository locally.

   The method a lit bit more difficult, but the project will be downloaded with the help
   of several commands, and not manually, as in the previous method. For this method
   you **need** to [install][4] the `Git Bash` on your computer, make some configuration and have a primary skill of
   using this system of version control.

    1. Enter your [name][5], [email][6] of GitHub account locally on your machine.
    2. Create an empty directory and initialize it as git repository. Use the next
       command - `git init`.
    3. Adds this repository to yours with name `origin` (you can change it, if you want):

       ```
       $ git remote add origin git@github.com:StasonMendelso/ClearSolutions.Technical-task.User-REST-Service.git
       ```
       But you need configure your SSH connection to your GitHub profile in Git Bash. See more [here][7].
       For viewing that the repository has been added successfully to your local
       repository, you need execute the next command and get the following result:

       ```
       $ git remote -v
       ```

       After this step your local repository has got a 'connection' to the remote project from the GitHub repository.

    4. For downloading the project use the following command:

       ```
       $ git pull origin main
       ```

       After these steps your project directory must contain the project files from
       GitHub repository. In addition to, you can create a new branch, make some
       changes and create a pull request for suggesting your improvements. Also, all
       changes are observed by the `git` and you can always make a rollback of
       all changes `git reset --hard`.

### Running the web-applcation

For running the web-application you ***have to built it with maven***, because some classes will be generated during
compiling project. So you have two choice how to build project with maven, as there is additional profile:

1) Default profile. It doesn't run anything else, only a standard workflow of building. Only unit-tests are ran. Use
   command:

```
    mvn clean install
```

2) Integration tests profile. This profile runs additional integration test which contains "\*IT\*" in separate phase.
   Use command:

```
    mvn clean install -Pintegration-tests
```

For running the app you need only run the main method in Java class. Also, you can
run app as a jar file, which is located in the [/target](target) directory, using the next command:

```
    java -jar user-rest-service-0.0.1-SNAPSHOT.jar
```

### Testing API [Postman]
You can download and export a Postman collection for testing developed API from [here](User%20REST%20service.postman_collection.json).

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management Tool

## Authors

* **Stanislav Hlova** - *All
  work* - [StasonMendelso](https://github.com/StasonMendelso)  [![wakatime](https://wakatime.com/badge/user/b33e0124-90c1-44a9-95a8-0f09b324ad70/project/f9f93ef8-2b3c-471c-a80c-b75b6b971c26.svg)](https://wakatime.com/badge/user/b33e0124-90c1-44a9-95a8-0f09b324ad70/project/f9f93ef8-2b3c-471c-a80c-b75b6b971c26)

[1]:https://nure.ua/

[4]:https://git-scm.com/downloads

[5]:https://docs.github.com/en/get-started/getting-started-with-git/setting-your-username-in-git

[6]:https://docs.github.com/en/account-and-profile/setting-up-and-managing-your-personal-account-on-github/managing-email-preferences/setting-your-commit-email-address

[7]:https://docs.github.com/en/authentication/connecting-to-github-with-ssh