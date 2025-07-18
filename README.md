# Daakia![DaakiaIcon48](https://github.com/appsbysalil/daakia/assets/34584327/5dcffba1-09c0-4c1a-9968-2b5600c5d8fb)
> An IntelliJ plugin designed to streamline API development directly within the IntelliJ IDE. 
It offers a comprehensive set of features to manage and test the rest APIs seamlessly, all without leaving your development environment.

## Features:

1. API Requests Management: Create, organize, and manage API requests within IntelliJ IDEA for efficient API development.
2. HTTP Methods Support: Support for various HTTP methods such as GET, POST, PUT, DELETE, etc., enabling easy interaction with RESTful APIs.
3. Request History: Maintain a history of recent requests for quick access and reference during API development.
4. Collections: Group related requests into collections for better organization and management of your API projects.
5. Request Body Editor: Conveniently edit request bodies using a user-friendly interface with support for JSON, XML, and other common formats.
6. Headers Management: Easily add, remove, and modify request headers to customize API requests according to your needs.
7. Authentication Support: Seamlessly handle authentication mechanisms such as Basic Auth, OAuth, API keys, etc., for secure API testing.
8. Response Viewer: View detailed responses including headers, status codes, and response bodies directly within IntelliJ IDEA for quick analysis.


## Installation:

1. Open IntelliJ IDEA.
2. Navigate to Preferences > Plugins.
3. Click on the Marketplace tab.
4. Search for "Daakia" and click Install.
5. Restart IntelliJ IDEA to activate the plugin.

## Usage:

1. After installation, access the Daakia plugin from the toolbar (![DaakiaIcon24](https://github.com/appsbysalil/daakia/assets/34584327/f75731fa-5640-4f19-9022-29b7533492e2)) at the bottom.
2. Create API requests and organize them into collections for efficient management.
3. Configure request parameters, headers, and authentication settings as needed.
4. Use the **Import** and **Export** icons in the main toolbar to load or save Postman collections.

### Importing and Exporting Postman Collections

1. Click the **Import** icon in the header to choose a Postman `.json` file.
2. The imported collection immediately appears in the tree and is stored using the SQLite persistence layer.
3. Click the **Export** icon to save the selected collection in Postman v2 format.

## Feedback and Support:

If you encounter any issues or have suggestions for improvement, please don't hesitate to reach out to us via the plugin's support channel or by filing an issue on the GitHub repository.

## License:

This plugin is distributed under the MIT License. See the LICENSE file for details.

## Change log

### Contributions by Codex

- Added SQLite persistence layer and DAO classes.
- Created Postman import/export utilities for collections.
- Implemented environment and variable data models with management UI.
- Enhanced plugin with GraphQL request support.
- Refined environment panel layout and text box alignment.
- Added dynamic row management for environment variables.
- Introduced placeholder-enabled `PasswordInputField` and updated authorization panel layout.
