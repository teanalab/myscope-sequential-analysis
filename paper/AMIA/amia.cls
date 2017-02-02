\NeedsTeXFormat{LaTeX2e}
\ProvidesClass{amia}[2011/02/21 v1.00 AMIA article class]
\LoadClassWithOptions{article}[10pt]

% Times new roman
\usepackage{times}

% Paper to US letter
\usepackage[paper=letterpaper, margin=1in]{geometry}
\usepackage{caption}
\usepackage{fullpage}
\usepackage{float}
\usepackage{titlesec}


\date{}

\AtBeginDocument{}

\renewcommand\title[1]{\gdef\@title{{\center\reset@font\fontsize{14pt}{10pt}\bfseries\rmfamily #1 }}}

% Institutes
\def\institutes#1{ \def\@institutes{ {\fontsize{12pt}{10pt}\bfseries\rmfamily #1} }}

\let\pre@maketitle\maketitle
\renewcommand\maketitle{
                        \begin{center}{\@title}\end{center} 
                        \begin{center}{\fontsize{12pt}{10pt}\bfseries\rmfamily \@author \\ \@institutes}\end{center}
}

%\setlength{\abovecaptionskip}{-10.0pt}
\setlength{\belowcaptionskip}{-10.0pt}

% Justify text
\setlength\parindent{0pt}


% Space between paragraphs
\parskip 6pt

% Section and subsection definition
%\@startsection{<type>}{<level>}{<indent>}{<beforeskip>}{<afterskip>}{<style>} 
\renewcommand\section{\@startsection {section}{1}{\z@}%
                     {-0.5\baselineskip} {0.01\baselineskip} %%
                     {\normalfont\fontsize{10pt}{10pt}\bfseries\rmfamily}}

\renewcommand\subsection{\@startsection {subsection}{1}{\z@}%
                     {-\baselineskip} {0.01\baselineskip} %%
                     {\normalfont\fontsize{10pt}{10pt}\bfseries\rmfamily}}
                     
\renewcommand\subsubsection{\@startsection {subsubsection}{1}{\z@}%
                     {-\baselineskip} {0.01\baselineskip} %%
                     {\normalfont\fontsize{10pt}{10pt}\bfseries\rmfamily}}


\thispagestyle{empty}
\pagestyle{empty}

\endinput
% End of amia.cls